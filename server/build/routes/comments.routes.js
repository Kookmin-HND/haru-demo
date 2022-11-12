"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.router = exports.path = void 0;
const express_1 = require("express");
const app_data_source_1 = __importDefault(require("../app-data-source"));
const comment_1 = require("../entity/comment");
const typeorm_1 = require("typeorm");
exports.path = "/comments";
exports.router = (0, express_1.Router)();
//게시물 하나에 속한 댓글들 조회
exports.router.get("/:postId", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const postId = Number(req.params.postId);
    try {
        // postId로 게시물 하나의 데이터를 가져온다
        const result = yield app_data_source_1.default.getRepository(comment_1.Comment).find({
            where: { post: (0, typeorm_1.Equal)(postId) },
        });
        return res.json(result);
    }
    catch (_a) {
        return res.status(500).send("댓글을 불러오는데 실패했습니다.");
    }
}));
//새 댓글 입력
exports.router.post("/:email", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    //req.body에 있는 정보를 바탕으로 새로운 게시물 데이터를 생성한다.
    const writer = req.params.email;
    const post = app_data_source_1.default
        .getRepository(comment_1.Comment)
        .create(Object.assign(Object.assign({}, req.body), { post: req.body.postId, writer }));
    const result = yield app_data_source_1.default.getRepository(comment_1.Comment).save(post);
    return res.json(result);
}));
//게시물 삭제요청 , deleted가 true라면 프론트에서 '삭제된 댓글입니다' 표시
exports.router.delete("/:commentId", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const commentId = Number(req.params.commentId);
    const result = yield app_data_source_1.default.getRepository(comment_1.Comment).update({
        id: commentId,
    }, { deleted: true });
    //affected : 0 실패, affected : 1 성공
    if (!result.affected)
        return res.status(400).send("댓글 삭제에 실패했습니다.");
    return res.json(result);
}));
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
