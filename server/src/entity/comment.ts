import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  Generated,
  UpdateDateColumn,
  ManyToOne,
} from "typeorm";
import { Post } from "./post";

//필요한 데이터베이스 스키마 entity에 생성

@Entity()
export class Comment {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ nullable: false })
  writer: string;

  @Column({ nullable: false })
  content: string;

  @ManyToOne((type) => Post, (post) => post.id)
  post: Post;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;
}
