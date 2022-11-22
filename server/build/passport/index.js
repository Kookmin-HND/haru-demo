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
const passport_1 = __importDefault(require("passport"));
const bcrypt_1 = __importDefault(require("bcrypt"));
const user_1 = require("../entity/user");
const app_data_source_1 = __importDefault(require("../app-data-source"));
const passport_local_1 = require("passport-local");
const passport_jwt_1 = require("passport-jwt");
const passportConfig = {
    usernameField: "email",
    passwordField: "password",
    session: false,
};
const passportVerify = (email, password, done) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        const user = yield app_data_source_1.default.getRepository(user_1.User).findOneBy({ email: email });
        if (!user) {
            return done(null, false, { reason: "존재하지 않는 사용자입니다." });
        }
        const result = yield bcrypt_1.default.compare(password, user.password);
        if (!result) {
            return done(null, false, { reason: "올바르지 않은 비밀번호 입니다." });
        }
        done(null, user);
    }
    catch (error) {
        console.error(error);
        done(error);
    }
});
/* 다른 해싱 방법
const salt = user.user_salt;
    const hashedPW = c
    rypto
      .createHash("sha512")
      .update(password + salt)
      .digest("base64"); */
const cookieExtractor = (req) => {
    const { token } = req.cookies;
    return token;
};
const JWTConfig = {
    jwtFromRequest: cookieExtractor,
    secretOrKey: process.env.JWT_KEY,
};
const JWTVerify = (token, done) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        if (!token) {
            console.log("token이 없습니다.");
            return done(null, false, { reason: "token이 없습니다." });
        }
        const user = yield app_data_source_1.default.getRepository(user_1.User).findOneBy({ email: token.email });
        if (!user) {
            console.log("token과 맞는 user가 없습니다.");
            return done(null, false, { reason: "token과 맞는 user가 없습니다." });
        }
        return done(null, user);
    }
    catch (error) {
        console.log("123");
        console.error(error);
        done(error);
    }
});
function passportOpt() {
    passport_1.default.use("local", new passport_local_1.Strategy(passportConfig, passportVerify));
    passport_1.default.use("jwt", new passport_jwt_1.Strategy(JWTConfig, JWTVerify));
}
exports.default = passportOpt;
