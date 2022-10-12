import { Request, Response, Router } from "express";
import { Equal } from "typeorm";
import myDataSource from "../app-data-source";
import { Todo } from "../entity/todo";
import { User } from "../entity/user";

interface TodoParams {
  email: string;
}

interface TodoRequestBody {
  id: number | null;
  folder: string;
  content: string;
  dates: string[];
}

type TodoResponseBody = Todo[];

export const path = "/todos";
export const router = Router();

// 사용자의 모든 todo를 반환한다.
router.get(
  "/:email",
  async (req: Request<TodoParams>, res: Response<TodoResponseBody>) => {
    const result: TodoResponseBody = [];

    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;

    // 이 사용자가 작성한 모든 todo를 가져온다.
    const todos = await myDataSource.getRepository(Todo).findBy({
      writer: Equal(writer),
    });

    // 위에서 가져온 데이터를 결과값에 추가한다.
    result.push(...todos);

    // 이 사용자가 가지고 있는 모든 todo를 반환한다.
    return res.send(result);
  }
);

// 사용자로부터 folder, content, dates를 받아서 todo table에 데이터를 저장한다.
router.post(
  "/:email",
  async (req: Request<TodoParams, {}, TodoRequestBody>, res: Response) => {
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;

    const { folder, content, dates } = req.body;

    // 혹시 입력으로부터 빠져있는 값이 있는지 확인한다.
    if (!folder || !content || !dates || (dates && !dates.length)) {
      return res
        .status(400)
        .send("folder, content, dates 중 하나의 값이 없습니다.");
    }

    const result: Todo[] = [];
    for (const date of dates) {
      // 입력 값에 따른 데이터를 생성한다.
      const todo = await myDataSource.getRepository(Todo).create({
        writer,
        folder,
        content,
        date,
        completed: false,
      });

      // 위에서 생성한 todo 데이터를 table에 저장한다.
      await myDataSource.getRepository(Todo).save(todo);
      result.push(todo);
    }
    return res.send(result);
  }
);

// 사용자로부터 입력받은 데이터(folder, content, date)를 해당하는 todo를 id값을 기준으로 찾아 변경한다.
router.patch(
  "/",
  async (req: Request<{}, {}, TodoRequestBody>, res: Response) => {
    // todo data의 id값을 가져온다.
    const id = req.body.id;
    if (!id) {
      return res.status(400).send("id가 존재하지 않습니다.");
    }

    // request body로부터 데이터를 가져온다.
    const { folder, content, dates } = req.body;

    if ((folder && !folder.trim()) || (content && !content.trim())) {
      return res.status(400).send("folder 또는 content가 비어있습니다.");
    }

    if (!dates || (dates && dates.length != 1)) {
      return res
        .status(400)
        .send("dates의 입력이 없거나, 두 개 이상의 날짜가 선택되었습니다.");
    }
    const date = dates[0];

    // todo 데이터를 업데이트 한다.
    const result = await myDataSource.getRepository(Todo).update(id, {
      folder,
      content,
      date,
    });

    return res.send(result);
  }
);

// 사용자로부터 todo id값을 입력받아 해당 데이터를 삭제한다.
router.delete("/", async (req: Request, res: Response) => {
  const id = req.body.id;

  if (!id) {
    return res.status(400).send("id가 존재하지 않습니다");
  }

  const result = await myDataSource.getRepository(Todo).delete(id);
  return res.send(result);
});
