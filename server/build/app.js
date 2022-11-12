"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
require("./env.ts");
const express_1 = __importDefault(require("express"));
const app_data_source_1 = __importDefault(require("./app-data-source"));
const routes_1 = __importDefault(require("./routes"));
const passport_1 = __importDefault(require("passport"));
const passport_2 = __importDefault(require("./passport"));
const cookie_parser_1 = __importDefault(require("cookie-parser"));
// establish database connection
app_data_source_1.default
    .initialize()
    .then(() => {
    console.log("Data Source has been initialized!");
})
    .catch((err) => {
    console.error("Error during Data Source initialization:", err);
});
const app = (0, express_1.default)();
app.use(express_1.default.json());
app.use((0, cookie_parser_1.default)());
app.use(passport_1.default.initialize());
(0, passport_2.default)();
app.get("/", (req, res, next) => {
    console.log("First page!");
    res.json({ "Frist page!": "hi" });
});
app.get("/welcome", (req, res, next) => {
    res.send("welcome!!");
});
//routes/index.ts 연결
//api 요청 경로 : localhost:8000/api/~~
app.use("/api", routes_1.default);
app.listen("8000", () => {
    console.log(`
  ################################################
  🛡️  Server listening on port: 8000
  ################################################
`);
});
