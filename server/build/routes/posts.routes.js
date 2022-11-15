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
const post_1 = require("../entity/post");
const typeorm_1 = require("typeorm");
const multer_1 = __importDefault(require("multer"));
exports.path = "/posts";
exports.router = (0, express_1.Router)();
const multerConfig = {
    storage: multer_1.default.diskStorage({
        destination: "images/",
        filename: function (req, file, cb) {
            cb(null, file.originalname);
        },
    }),
};
const upload = (0, multer_1.default)(multerConfig); //dest : 저장 위치
// multer test
exports.router.post("/upload", upload.single("img"), (req, res) => {
    const file = req.files;
    console.log(file);
    res.json(req.file);
});
//postid 이후 최근 게시물 50개 정보 sns fragment에서 보여지는 정보
exports.router.get("/recent/:postId", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    //마지막으로 읽은 postId를 바탕으로 이후 게시물 50개를 가져온다
    const readedPostId = Number(req.params.postId);
    const result = yield app_data_source_1.default.getRepository(post_1.Post).find({
        where: { id: (0, typeorm_1.LessThan)(readedPostId) },
        take: 50,
        order: { id: "DESC" },
    });
    if (!result.length)
        return res.status(400).send("게시물이 존재하지 않습니다.");
    return res.json(result);
}));
//게시물 하나의 정보 조회
exports.router.get("/:postId", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const postId = Number(req.params.postId);
    try {
        //postId로 게시물 하나의 데이터를 가져온다
        const result = yield app_data_source_1.default.getRepository(post_1.Post).findOneOrFail({
            where: { id: postId },
        });
        return res.json(result);
    }
    catch (_a) {
        return res.status(400).send("게시물이 존재하지 않습니다.");
    }
}));
//게시물 입력
exports.router.post("/:email", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    //req.body에 있는 정보를 바탕으로 새로운 게시물 데이터를 생성한다.
    console.log("hererere@@@@@@@@");
    console.log(req);
    const writer = req.params.email;
    const post = app_data_source_1.default.getRepository(post_1.Post).create(Object.assign(Object.assign({}, req.body), { writer }));
    const result = yield app_data_source_1.default.getRepository(post_1.Post).save(post);
    return res.json(result);
}));
//게시물 삭제요청
exports.router.delete("/:postId", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const postId = Number(req.params.postId);
    const result = yield app_data_source_1.default.getRepository(post_1.Post).delete(postId);
    //affected : 0 실패, affected : 1 성공
    if (!result.affected)
        return res.status(400).send("게시물 삭제에 실패했습니다.");
    return res.json(result);
}));
//게시물 수정
exports.router.patch("/:postId", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const postId = Number(req.params.postId);
    const content = req.body.content;
    // id가 postId에 해당하는 게시글의 content 수정
    const result = yield app_data_source_1.default
        .getRepository(post_1.Post)
        .update({ id: postId }, { content });
    //affected : 0 실패, affected : 1 성공
    if (!result.affected)
        return res.status(400).send("게시물 수정에 실패했습니다.");
    return res.json(result);
}));
