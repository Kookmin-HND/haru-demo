import { Router } from "express";
import * as tests from "./tests.routes";
import * as todos from "./todos.routes";

const router = Router();
// router.use(example.path, example.router);
// 여기에 새로 만든 라우터를 import하고 추가하면 된다.
router.use(tests.path, tests.router);
router.use(todos.path, todos.router);

export default router;
