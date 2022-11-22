import { Router } from "express";
import * as tests from "./tests.routes";
import * as todos from "./todos.routes";
import * as posts from "./posts.routes";
import * as users from "./users.routes";
import * as comments from "./comments.routes";
import * as likes from "./likes.routes";

const router = Router();
// router.use(example.path, example.router);
// 여기에 새로 만든 라우터를 import하고 추가하면 된다.
router.use(tests.path, tests.router);
router.use(users.path, users.router);
router.use(todos.path, todos.router);
router.use(posts.path, posts.router);
router.use(comments.path, comments.router);
router.use(likes.path, likes.router);

export default router;
