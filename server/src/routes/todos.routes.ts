import { Request, Response, Router } from "express";
import { Equal } from "typeorm";
import myDataSource from "../app-data-source";
import { Todo } from "../entity/todo";
import { TodoDate } from "../entity/todoDate";
import { User } from "../entity/user";

interface TodoParamsInterface {
  email: string;
}

interface TodoInputInterface {
  folder: string;
  content: string;
  dates: string[];
}

interface TodoResponseBody {
  todos: Todo[];
  todoDates: TodoDate[][];
}

export const path = "/todos";
export const router = Router();

//http://localhost:8000/api/todos
router.get(
  "/:email",
  async function (
    req: Request<TodoParamsInterface>,
    res: Response<TodoResponseBody>
  ) {
    const result: TodoResponseBody = { todos: [], todoDates: [] };

    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;

    // 이 사용자가 작성한 모든 todo를 가져온다.
    const todos = await myDataSource.getRepository(Todo).findBy({
      writer: Equal(writer),
    });

    // 위에서 가져온 데이터를 결과값에 추가한다.
    result.todos = todos;

    for (let i = 0; i < todos.length; i++) {
      // 이 사용자가 작성한 모든 todo에 해당하는 todoDate를 가져온다.
      const todoDates = await myDataSource.getRepository(TodoDate).findBy({
        writer: Equal(writer),
        todoId: Equal(todos[i].todoId),
      });

      // 위에서 가져온 데이터를 결과값에 추가한다.
      result.todoDates.push(todoDates);
    }

    // 이 사용자가 가지고 있는 모든 todo를 반환한다.
    return res.send(result);
  }
);

router.post(
  "/:email",
  async function (
    req: Request<TodoParamsInterface, {}, TodoInputInterface>,
    res: Response
  ) {
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;

    // todoId 값을 가져오기 위해 user 데이터에 접근한다.
    const user = await myDataSource.getRepository(User).findOneBy({
      email: Equal(writer),
    });

    if (!user) {
      return res.status(400).send("등록되지 않은 사용자입니다.");
    }

    const todoId = user.todoId;

    const { folder, content, dates } = req.body;

    // 혹시 입력으로부터 빠져있는 값이 있는지 확인한다.
    if (!folder || !content || !dates || (dates && !dates.length)) {
      return res
        .status(400)
        .send("folder, content, dates 중 하나의 값이 없습니다.");
    }

    // todo를 추가한 유저의 todoId 값을 증가한다.
    await myDataSource.getRepository(User).update(user.id, {
      todoId: todoId + 1,
    });

    // todo table에 접근하여 입력 값에 따른 데이터를 생성한다.
    const todo = await myDataSource.getRepository(Todo).create({
      writer,
      todoId,
      folder,
      content,
    });

    dates.forEach(async (date) => {
      // todo-date table에 접근하여 모든 일자에 따른 데이터를 생성 및 저장한다.
      const todoDate = await myDataSource.getRepository(TodoDate).create({
        writer,
        todoId,
        date,
        completed: false,
      });

      // 위에서 생성한 todoDate 데이터를 table에 저장한다.
      await myDataSource.getRepository(TodoDate).save(todoDate);
    });

    // 위에서 생성한 todo 데이터를 table에 저장한다.
    const results = await myDataSource.getRepository(Todo).save(todo);
    return res.send(results);
  }
);
