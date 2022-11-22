import aws from "aws-sdk";
import multer from "multer";
import multerS3 from "multer-s3";
import { S3Client } from "@aws-sdk/client-s3";
import { v4 } from "uuid";

const multerFilter = (req: any, file: any, cb: any) => {
  if (file.mimetype.startsWith("image")) {
    cb(null, true);
  } else {
    cb({ msg: "image 파일만 업로드 가능합니다." }, false);
  }
};

const s3 = new S3Client({
  credentials: {
    accessKeyId: process.env.S3_ACCESS_KEY as string,
    secretAccessKey: process.env.S3_SECRET_KEY as string,
  },
  region: "ap-northeast-2",
});

//S3 연동 config 지정
export const awsUpload = multer({
  storage: multerS3({
    s3,
    bucket: process.env.BUCKET_NAME as string,
    metadata: function (req: any, file: any, cb) {
      cb(null, { fieldName: file.fieldname });
    },
    key: function (req: any, file, cb) {
      //원본파일의 파일 타입을 알아내서 파일 지정
      const fileType = "." + file.mimetype.split("/")[1];
      const fileDirectory = "post/";
      const fileName = file.fieldname + v4();
      const fileLocation = fileDirectory + fileName + fileType;
      cb(null, fileLocation);
    },
    acl: "public-read",
    contentType: multerS3.AUTO_CONTENT_TYPE,
  }),
  fileFilter: multerFilter,
});
