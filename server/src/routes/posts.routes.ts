import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { Post } from "../entity/post";
import { MoreThan } from "typeorm";

export const path = "/posts";
export const router = Router();

interface PostParams {
  postId: number;
}

// type RecentPostResponseBody = Post[];

//postid 이후 최근 게시물 50개 정보 sns fragment에서 보여지는 정보
router.get(
  "/recent/:postId",
  async (req: Request<PostParams>, res: Response) => {
    //마지막으로 읽은 post Id
    const readedPostId = Number(req.params.postId);
    const result = await myDataSource.getRepository(Post).find({
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
    const result = await myDataSource.getRepository(Post).findOneOrFail({
      where: { id: postId },
    });
    return res.json(result);
  } catch {
    return res.status(400).send("게시물이 존재하지 않습니다.");
  }
});

//게시물 입력
router.post("/", async function (req: Request, res: Response) {
  const post = myDataSource.getRepository(Post).create(req.body);
  const result = await myDataSource.getRepository(Post).save(post);

  return res.json(result);
});

//게시물 삭제요청
router.delete(
  "/:postId",
  async function (req: Request<PostParams>, res: Response) {
    const postId = Number(req.params.postId);
    const result = await myDataSource.getRepository(Post).delete(postId);

    //affected : 0 실패, affected : 1 성공
    if (!result.affected)
      return res.status(400).send("게시물 삭제에 실패했습니다.");

    return res.json(result);
  }
);

//게시물 수정
router.patch(
  "/:postId",
  async function (req: Request<PostParams>, res: Response) {
    const postId = Number(req.params.postId);
    const content: string = req.body.content;

    // id가 postId에 해당하는 게시글의 content 수정
    const result = await myDataSource
      .getRepository(Post)
      .update({ id: postId }, { content });

    //affected : 0 실패, affected : 1 성공
    if (!result.affected)
      return res.status(400).send("게시물 수정에 실패했습니다.");

    return res.json(result);
  }
);
