import { Router } from "express";
import express, { Request, Response, NextFunction } from "express";
import myDataSource from "../app-data-source";
import { User } from "../entity/user";

export const path = "/users";
export const router = Router();

router.get("/", async function (req: Request, res: Response) {});
