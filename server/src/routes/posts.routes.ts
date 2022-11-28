import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import DB from "../app-data-source";
import { Post } from "../entity/post";
import { Equal, LessThan, MoreThan } from "typeorm";
import { awsUpload } from "../../config/multerConfig";
import { ImageFile } from "../entity/imageFile";
import { ProfileImageFile } from "../entity/profileImageFile";
import { User } from "../entity/user";

export const path = "/posts";
export const router = Router();

interface PostParams {
  postId: number;
}

interface MulterS3File extends Express.Multer.File {
  location: string;
}

// multer test
router.post("/upload", awsUpload.array("img"), async (req: Request, res: Response) => {
  const locationList: Array<String> = [];

  const filesList: MulterS3File[] = req.files as MulterS3File[];
  filesList.forEach((file) => {
    locationList.push(file.location);
  });

  console.log(locationList);
  // 업로드한 이미지 파일 url 전달
  res.json({ locationList });
});

//postid 이후 최근 게시물 20개 정보 sns fragment에서 보여지는 정보
router.get("/recent/:postId", async (req: Request<PostParams>, res: Response) => {
  //마지막으로 읽은 postId를 바탕으로 이후 게시물 50개를 가져온다
  const readedPostId = Number(req.params.postId);

  //이미지, 코멘트 정보 조인해서 불러오기
  const result = await DB.getRepository(Post)
    .createQueryBuilder("post")
    .where("post.id <= :id", { id: readedPostId })
    .orderBy({ "post.id": "DESC" })
    .leftJoin("post.user", "user")
    .addSelect("user.id")
    .addSelect("user.name")
    .addSelect("user.email")
    .leftJoinAndSelect("user.images", "image")
    .leftJoinAndSelect("post.imageFiles", "imageFiles.url")
    .leftJoinAndSelect("post.comments", "comment.post")
    .leftJoinAndSelect("post.likes", "like.post")
    .take(20)
    .getMany();

  if (!result.length) return res.status(400).send("게시물이 존재하지 않습니다.");

  return res.json(result);
});

//게시물 하나의 정보 조회
router.get("/:postId", async (req: Request<PostParams>, res: Response) => {
  const postId = Number(req.params.postId);

  try {
    //postId로 게시물 하나의 데이터를 가져온다
    const result = await DB.getRepository(Post).findOneOrFail({
      where: { id: postId },
    });
    return res.json(result);
  } catch {
    return res.status(400).send("게시물이 존재하지 않습니다.");
  }
});


//게시물 삭제
router.delete("/:postId", async (req: Request, res: Response) => {
  const postId = Number(req.params.postId);
  try {
    //postId로 게시물 하나의 데이터를 가져온다
    const result = await DB.getRepository(Post).delete(postId);
    return res.json(result);
  } catch {
    return res.status(400).send("게시물이 존재하지 않습니다.");
  }
});


//게시물 하나가 갖고 있는 이미지 url 조회
router.get("/:postId/images", async (req: Request<PostParams>, res: Response) => {
  const postId = Number(req.params.postId);

  //postId로 게시물 하나의 데이터를 가져온다
  const result = await DB.getRepository(ImageFile).find({
    where: { post: Equal(postId) },
  });
  return res.json(result);
});

//게시물 입력 이미지 추가
router.post("/:userId", awsUpload.array("images"), async (req: Request, res: Response) => {
  //req.body에 있는 정보를 바탕으로 새로운 게시물 데이터를 생성한다.
  const userId = Number(req.params.userId);
  const category = req.body.category;
  const content = req.body.content;

  //이미지 S3에 저장한 url
  const locationList: Array<string> = [];
  const filesList: MulterS3File[] = req.files as MulterS3File[];
  filesList.forEach((file) => {
    locationList.push(file.location);
  });

  const user = new User()
  user.id = userId

  const imageFileList: ImageFile[] = [];
  const post = DB.getRepository(Post).create({ user, category, content});
  const result = await DB.getRepository(Post).save(post);

  locationList.forEach((item) => {
    imageFileList.push(DB.getRepository(ImageFile).create({ post, url: item }));
  });
  await DB.getRepository(ImageFile).save(imageFileList);
  // 업로드한 이미지 파일 url 전달
  return res.json({ ...result, locationList });
});

//게시물 삭제요청
router.delete("/:postId", async (req: Request<PostParams>, res: Response) => {
  const postId = Number(req.params.postId);
  const result = await DB.getRepository(Post).delete(postId);

  //affected : 0 실패, affected : 1 성공
  if (!result.affected) return res.status(400).send("게시물 삭제에 실패했습니다.");

  return res.json(result);
});

//게시물 수정
router.patch("/:postId", async (req: Request<PostParams>, res: Response) => {
  const postId = Number(req.params.postId);
  const content: string = req.body.content;

  // id가 postId에 해당하는 게시글의 content 수정
  const result = await DB.getRepository(Post).update({ id: postId }, { content });

  //affected : 0 실패, affected : 1 성공
  if (!result.affected) return res.status(400).send("게시물 수정에 실패했습니다.");
  return res.json(result);
});



//프로필 이미지 입력 추가
router.post("/profile/:userId", awsUpload.array("images"), async (req: Request, res: Response) => {
  const userId = Number(req.params.userId);

  //이미지 S3에 저장한 url
  const locationList: Array<string> = [];
  const filesList: MulterS3File[] = req.files as MulterS3File[];
  filesList.forEach((file) => {
    locationList.push(file.location);
  });

  const user = new User()
  user.id = userId
  const imageFileList: ProfileImageFile[] = [];

  locationList.forEach((item) => {
    imageFileList.push(DB.getRepository(ProfileImageFile).create({user, url: item}));
  });
  await DB.getRepository(ProfileImageFile).save(imageFileList);
  // 업로드한 이미지 파일 url 전달
  return res.json({ locationList });
});

//유저 id에 해당하는 profile image url들 리턴
router.get("/profile/:userId", async (req: Request, res: Response) => {
  const userId = Number(req.params.userId);

  //user, profile image 정보 조인해서 불러오기
  const result = await DB.getRepository(User)
    .createQueryBuilder("user")
    .where("user.id = :id", { id: userId })
    .leftJoinAndSelect("user.images", "images.url")
    .getOne()

  if (result == null) {
    return res.send("해당 사용자가 존재하지 않습니다.")
  }
  return res.json(result.images);
});


router.use((error:Error, req:Request, res:Response, next:NextFunction) => {
  res.send("posts router error");
})