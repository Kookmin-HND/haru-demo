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
const typeorm_1 = require("typeorm");
const app_data_source_1 = __importDefault(require("../app-data-source"));
const todo_1 = require("../entity/todo");
const todo_log_1 = require("../entity/todo-log");
exports.path = "/todos";
exports.router = (0, express_1.Router)();
// 사용자의 모든 todo를 반환한다.
exports.router.get("/:email", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const result = [];
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;
    // 이 사용자가 작성한 모든 todo를 가져온다.
    const todos = yield app_data_source_1.default.getRepository(todo_1.Todo).findBy({
        writer: (0, typeorm_1.Equal)(writer),
    });
    // 위에서 가져온 데이터를 결과값에 추가한다.
    result.push(...todos);
    // 이 사용자가 가지고 있는 모든 todo를 반환한다.
    return res.json(result);
}));
// 사용자로부터 folder, content, dates, days를 받아서 todo table에 데이터를 저장한다.
exports.router.post("/:email", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    // email은 로그인된 사용자의 이메일을 가져오므로 항상 있다고 가정한다.
    const writer = req.params.email;
    const { folder, content, dates, days } = req.body;
    // 혹시 입력으로부터 빠져있는 값이 있는지 확인한다.
    if (!folder ||
        !content ||
        !dates ||
        (dates && !dates.length) ||
        !days ||
        (days && !days.length)) {
        return res
            .status(400)
            .send("folder, content, dates, days 중 하나의 값이 없습니다.");
    }
    // 입력 값에 따른 데이터를 생성한다.
    const todo = yield app_data_source_1.default.getRepository(todo_1.Todo).create({
        writer,
        folder,
        content,
        dates: JSON.stringify(dates),
        days: JSON.stringify(days),
    });
    // 위에서 생성한 todo 데이터를 table에 저장한다.
    yield app_data_source_1.default.getRepository(todo_1.Todo).save(todo);
    return res.json(todo);
}));
// 사용자로부터 입력받은 데이터(folder, content, dates, days)를 해당하는 todo를 id값을 기준으로 찾아 변경한다.
exports.router.patch("/", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    // todo data의 id값을 가져온다.
    const id = req.body.id;
    if (!id) {
        return res.status(400).send("id가 존재하지 않습니다.");
    }
    // request body로부터 데이터를 가져온다.
    const { folder, content, dates, days } = req.body;
    if ((folder && !folder.trim()) ||
        (content && !content.trim()) ||
        (dates && !dates.length)) {
        return res
            .status(400)
            .send("folder 또는 content 또는 date가 비어있습니다.");
    }
    // todo 데이터를 업데이트 한다.
    const result = yield app_data_source_1.default.getRepository(todo_1.Todo).update(id, {
        folder,
        content,
        dates: JSON.stringify(dates),
        days: JSON.stringify(days),
    });
    return res.json(result);
}));
// todo id값을 입력받아 해당하는 데이터를 찾은 후 date를 제거하거나, 추가한다.
// 그리고 date를 제거하게 되면, todo-log에 추가하고,
// 추가하게 된다면, todo-log에 있는지 확인하고, 제거한다.
exports.router.patch("/check", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const { id, date } = req.body;
    if (!date || (date && !date.trim())) {
        return res.status(400).send("date가 비어있습니다.");
    }
    const todo = yield app_data_source_1.default.getRepository(todo_1.Todo).findOneBy({
        id: (0, typeorm_1.Equal)(id),
    });
    if (!todo) {
        return res.status(400).send("Todo Data를 찾을 수 없습니다.");
    }
    const todoLog = yield app_data_source_1.default.getRepository(todo_log_1.TodoLog).findOneBy({
        todoId: (0, typeorm_1.Equal)(id),
        completedAt: (0, typeorm_1.Equal)(date),
    });
    const result = [];
    if (todoLog) {
        // Log에 있으므로 삭제한다.
        result.push(yield app_data_source_1.default.getRepository(todo_1.Todo).update(todo.id, {
            dates: JSON.stringify([...JSON.parse(todo.dates), date]),
        }));
        result.push(yield app_data_source_1.default.getRepository(todo_log_1.TodoLog).delete(todoLog.id));
    }
    else {
        // Log에 없으므로 추가한다.
        result.push(yield app_data_source_1.default.getRepository(todo_1.Todo).update(todo.id, {
            dates: JSON.stringify(JSON.parse(todo.dates).filter((v) => v != date)),
        }));
        result.push(yield app_data_source_1.default.getRepository(todo_log_1.TodoLog).create({
            todoId: id,
            completedAt: date,
        }));
    }
    return res.json(result);
}));
// 사용자로부터 todo id값을 입력받아 해당 데이터를 삭제한다.
exports.router.delete("/", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const id = req.body.id;
    if (!id) {
        return res.status(400).send("id가 존재하지 않습니다");
    }
    const result = yield app_data_source_1.default.getRepository(todo_1.Todo).delete(id);
    return res.json(result);
}));
