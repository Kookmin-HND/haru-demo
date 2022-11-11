import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { Post } from "../entity/post";
import { Comment } from "../entity/comment";
import { Equal } from "typeorm";

export const path = "/comments";
export const router = Router();

interface CommentParams {
  commentId: number;
}

interface PostParams {
  postId: number;
}

//게시물 하나에 속한 댓글들 조회
router.get("/:postId", async (req: Request<PostParams>, res: Response) => {
  const postId = Number(req.params.postId);
  try {
    // postId로 게시물 하나의 데이터를 가져온다
    const result = await myDataSource.getRepository(Comment).find({
      where: { post: Equal(postId) },
    });
    return res.json(result);
  } catch {
    return res.status(500).send("댓글을 불러오는데 실패했습니다.");
  }
});

//새 댓글 입력
router.post("/:email", async (req: Request, res: Response) => {
  //req.body에 있는 정보를 바탕으로 새로운 게시물 데이터를 생성한다.
  const writer = req.params.email;

  const post = myDataSource.getRepository(Comment).create({ ...req.body, post: req.body.postId, writer });
  const result = await myDataSource.getRepository(Comment).save(post);

  return res.json(result);
});

//게시물 삭제요청 , deleted가 true라면 프론트에서 '삭제된 댓글입니다' 표시
router.delete("/:commentId", async (req: Request<CommentParams>, res: Response) => {
  const commentId = Number(req.params.commentId);
  const result = await myDataSource.getRepository(Comment).update(
    {
      id: commentId,
    },
    { deleted: true }
  );

  //affected : 0 실패, affected : 1 성공
  if (!result.affected) return res.status(400).send("댓글 삭제에 실패했습니다.");

  return res.json(result);
});

// //댓글 수정 -> 필요 없음
// router.patch("/:commentId", async (req: Request, res: Response) => {
//   const commentId = Number(req.params.commentId);
//   const content: string = req.body.content;

//   // id가 commentId에 해당하는 게시글의 content 수정
//   const result = await myDataSource
//     .getRepository(Post)
//     .update({ id: commentId }, { content });

//   //affected : 0 실패, affected : 1 성공
//   if (!result.affected)
//     return res.status(400).send("댓글 수정에 실패했습니다.");

//   return res.json(result);
// });
