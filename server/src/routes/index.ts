import { Router } from "express";
import * as tests from "./tests.routes";
import * as users from "./users.routes";

const router = Router();
// router.use(example.path, example.router);
// 여기에 새로 만든 라우터를 import하고 추가하면 된다.
router.use(tests.path, tests.router);
router.use(users.path, users.router);

export default router;
