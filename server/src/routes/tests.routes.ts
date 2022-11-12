import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import DB from "../app-data-source";
import { User } from "../entity/user";

export const path = "/tests";
export const router = Router();

//예시
//http://localhost:8000/api/tests/
router.get("/", (req: Request, res: Response, next: NextFunction) => {
  res.send("test!!");
});

//예시 DB호출하는 api
//http://localhost:8000/api/tests/users/
router.get("/users", async function (req: Request, res: Response) {
  const users = await DB.getRepository(User).find();
  console.log("get /users 호출됨!");
  res.json(users);
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
