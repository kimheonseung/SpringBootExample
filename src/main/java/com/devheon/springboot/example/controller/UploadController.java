package com.devheon.springboot.example.controller;

import com.devheon.springboot.example.dto.UploadResultDTO;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
public class UploadController {
    @Value("${com.devheon.upload.path}")    /* application.properties의 변수 */
    private String uploadPath;

    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) {
        List<UploadResultDTO> resultDTOList = new ArrayList<>();

        for(MultipartFile uploadFile : uploadFiles) {

            /**
             * 파일 형식 체크
             */
            log.info("request file type : " + uploadFile.getContentType());
            if(!uploadFile.getContentType().startsWith("image")) {
                log.warn("this file is not image type");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            /* IE, Edge는 전체 경로가 들어옴. -> 실제 파일 이름을 가져온다 */
            String originalName = uploadFile.getOriginalFilename();
            String fileName = originalName.substring(originalName.lastIndexOf(File.separator) + 1);
            log.info("fileName : " + fileName);

            /**
             * 파일 저장 고려 사항
             * 1. 업로드된 확장자가 이미지만 가능하도록
             *     - 첨부파일을 이용해 쉘스크립트 파일 등을 업로드 하여 공격하는 기법(웹쉘)들도 있으므로
             *       브라우저에서 파일을 업로드하는 순간이나 서버에서 파일을 저장하는 순간에도 이를 검사하는 과정이 필요하다
             *       MultipartFile에서 제공하는 getContentType()을 이용하여 처리
             * 2. 동일한 이름의 파일이 업로드 된다면 기존 파일을 덮어쓰는 문제
             *     - 고유 이름을 생성하여 파일 이름으로 사용한다.
             *     -     1. 가장 많이 사용하는 방식인 시간 값을 파일 이름에 추가
             *     -     2. UUID를 이용하여 고유값을 만들어 사용
             *     => UUID_파일명 형태로 저장하기로 한다.
             * 3. 업로드된 파일을 저장하는 폴더의 용량
             *     - 업로드 되는 파일들을 동일한 폴더에 넣으면 너무 많은 파일이 쌓이고 성능도 저하된다.
             *     - 운영체제에 따라 하나의 폴더에 넣을 수 있는 파일 수 제한이 있다. (FAT32는 65534개)
             *     - 일반적으로 파일이 저장되는 시점의 년/월/일 폴더를 생성하여 파일을 저장한다.
             */

            /* 날짜 폴더 생성 */
            String folderPath = makeFolder();

            /* UUID 생성 */
            String uuid = UUID.randomUUID().toString();

            /* 저장할 파일 이름 중간에 _ 구분 */
            String saveName = uploadPath + File.separator +
                    folderPath + File.separator +
                    uuid + "_" + fileName;

            log.info("save target : " + saveName);

            Path savePath = Paths.get(saveName);

            try {
                /**
                 * transferTo로 MultipartFile 객체를 저장한다.
                 */
                uploadFile.transferTo(savePath);

                /**
                 * Thumbnailator를 이용하여 썸네일 생성
                 * 원본 이미지를 출력하면 데이터를 많이 소비해야 하므로 섬네일을 만들어 전송해준다.
                 * 섬네일 처리는 다음 과정을 거친다.
                 * 1. 업로드된 파일을 저장하고 썸네일을 만든다.
                 * 2. 썸네일 파일은 맨 앞에 's_'를 붙여 일반 파일과 구분
                 * 3. UploadResultDTO에 getThumbnailURL()을 추가하여 섬네일 경로를 img태그로 처리
                 */
                String thumbnailSaveName = uploadPath + File.separator +
                        folderPath + File.separator +
                        "s_" + uuid + "_" + fileName;
                File thumbnailFile = new File(thumbnailSaveName);
                /* 썸네일 저장 (in, out, width, height) */
                Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);

                /**
                 * 브라우저에 필요한 정보를 전달
                 * 1. 업로드된 파일의 원래 이름
                 * 2. 파일의 UUID값
                 * 3. 업로드된 파일의 저장경로
                 */
                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
            } catch(IOException e) {
                e.printStackTrace();
            }

        }    /* end for loop */

        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName, String size) {
        /**
         * JSON으로 반환된 업로드 결과를 화면에서 확인하기 위해서는
         * 1. 브라우저에서 링크를 통해 <img>태그를 추가한다.
         * 2. 서버에서 해당 URL이 호출되는 경우 이미지 파일 데이터를 브라우저로 전송해준다.
         * 이를 처리하기 위해 '/disply?fileName=xxxx'와 같은 URL 호출 시 이미지가 전송되도록
         */
        ResponseEntity<byte[]> result = null;

        try {
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("display fileName : " + srcFileName);
            File file = new File(uploadPath + File.separator + srcFileName);
            log.info("display taraget : " + file);

            /**
             * size값이 1이면 원본파일을 넘겨준다
             */
            if(size != null && size.equals("1"))
                file = new File(file.getParent(), file.getName().substring(2));

            HttpHeaders header = new HttpHeaders();

            /* MIME 타입 처리. 확장자에 따라 MIME타입을 다르게 설정한다. */
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            /* 파일 데이터 처리 */
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @PostMapping("/removeFile")
    public ResponseEntity<Boolean> removeFiles(String fileName) {
        /**
         * 업로드 파일의 삭제
         * 파일의 URL 자체가 '년/월/일/uuid_파일명'으로 구성되어있으므로 이를 통해 파일을 찾아 삭제한다.
         * 경로와 UUID가 포함된 파일 이름을 파라미터로 받아 삭제 결과를 Boolean 타입으로 리턴
         */
        String srcFileName = null;
        try {
            srcFileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(uploadPath + File.separator + srcFileName);
            boolean result = file.delete();

            File thumbnail = new File(file.getParent(), "s_"+file.getName());
            result = thumbnail.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("//", File.separator);

        /* make folder */
        File uploadPathFolder = new File(uploadPath, folderPath);

        if(!uploadPathFolder.exists())
            uploadPathFolder.mkdirs();

        log.info("uploadPathFolder : " + uploadPathFolder.getAbsolutePath());

        return folderPath;
    }
}