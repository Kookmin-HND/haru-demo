import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import DB from "../app-data-source";
import { Post } from "../entity/post";
import { MoreThan } from "typeorm";

export const path = "/posts";
export const router = Router();

interface PostParams {
  postId: number;
}

//postid 이후 최근 게시물 50개 정보 sns fragment에서 보여지는 정보
router.get(
  "/recent/:postId",
  async (req: Request<PostParams>, res: Response) => {
    //마지막으로 읽은 postId를 바탕으로 이후 게시물 50개를 가져온다
    const readedPostId = Number(req.params.postId);
    const result = await DB.getRepository(Post).find({
      where: { id: MoreThan(readedPostId) },
      take: 50,
      order: { id: "DESC" },
    });

    if (!result.length)
      return res.status(400).send("게시물이 존재하지 않습니다.");

    return res.json(result);
  }
);

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

//게시물 입력
router.post("/", async (req: Request, res: Response) => {
  //req.body에 있는 정보를 바탕으로 새로운 게시물 데이터를 생성한다.
  const post = DB.getRepository(Post).create(req.body);
  const result = await DB.getRepository(Post).save(post);

  return res.json(result);
});

//게시물 삭제요청
router.delete("/:postId", async (req: Request<PostParams>, res: Response) => {
  const postId = Number(req.params.postId);
  const result = await DB.getRepository(Post).delete(postId);

  //affected : 0 실패, affected : 1 성공
  if (!result.affected)
    return res.status(400).send("게시물 삭제에 실패했습니다.");

  return res.json(result);
});

//게시물 수정
router.patch("/:postId", async (req: Request<PostParams>, res: Response) => {
  const postId = Number(req.params.postId);
  const content: string = req.body.content;

  // id가 postId에 해당하는 게시글의 content 수정
  const result = await DB.getRepository(Post).update(
    { id: postId },
    { content }
  );

  //affected : 0 실패, affected : 1 성공
  if (!result.affected)
    return res.status(400).send("게시물 수정에 실패했습니다.");

  return res.json(result);
});
