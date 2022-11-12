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
const user_1 = require("../entity/user");
exports.path = "/tests";
exports.router = (0, express_1.Router)();
//예시
//http://localhost:8000/api/tests/
exports.router.get("/", (req, res, next) => {
    res.send("test!!");
});
//예시 DB호출하는 api
//http://localhost:8000/api/tests/users/
exports.router.get("/users", function (req, res) {
    return __awaiter(this, void 0, void 0, function* () {
        const users = yield app_data_source_1.default.getRepository(user_1.User).find();
        console.log("get /users 호출됨!");
        res.json(users);
    });
});
// router.get("/users/:id", async function (req: Request, res: Response) {
//   console.log("params:", req.params.id);
//   const results = await myDataSource.getRepository(User).findOneBy({
//     id: Number(req.params.id),
//   });
//   console.log("GET user", results);
//   return res.send(results);
// });
// router.post("/users", async function (req: Request, res: Response) {
//   console.log(req.body);
//   const user = await myDataSource.getRepository(User).create(req.body);
//   const results = await myDataSource.getRepository(User).save(user);
//   return res.send(results);
// });
// router.put("/users/:id", async function (req: Request, res: Response) {
//   const user = await myDataSource.getRepository(User).findOneBy({
//     id: Number(req.params.id),
//   });
//   if (user === null) return;
//   myDataSource.getRepository(User).merge(user, req.body);
//   const results = await myDataSource.getRepository(User).save(user);
//   return res.send(results);
// });
// router.delete("/users/:id", async function (req: Request, res: Response) {
//   const results = await myDataSource.getRepository(User).delete(req.params.id);
//   return res.send(results);
// });
