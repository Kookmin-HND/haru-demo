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
import { Comment } from "./comment";

//필요한 데이터베이스 스키마 entity에 생성
@Entity()
export class CommentLike {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  //게시물이 삭제되면 댓글도 삭제되도록 cascade
  @ManyToOne(() => Comment, (comment) => comment.likes, {
    onDelete: "CASCADE",
    nullable: false,
  })
  @JoinColumn()
  comment: Comment;

  @Column({ nullable: false })
  user: string;
}
