import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  Generated,
  UpdateDateColumn,
  ManyToOne,
  JoinColumn,
} from "typeorm";
import { Post } from "./post";

//필요한 데이터베이스 스키마 entity에 생성
@Entity()
export class Comment {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  //게시물이 삭제되면 댓글도 삭제되도록 cascade
  @ManyToOne(() => Post, (post) => post.comments, {
    onDelete: "CASCADE",
    nullable: false,
  })
  @JoinColumn()
  post: Post;

  @Column({ nullable: false })
  writer: string;

  @Column({ nullable: false })
  content: string;

  @Column()
  parentCommentId: number;

  @Column()
  deleted: boolean;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;
}
