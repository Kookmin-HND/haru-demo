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

  //대댓글 기능을 위한 부모의 comment id 추가
  @Column({ default: -1 })
  parentCommentId: number;

  //삭제된 댓글인지 표기하기 위한 컬럼
  @Column({ default: false })
  deleted: boolean;

  @CreateDateColumn({
    type: "timestamp",
    default: () => "CURRENT_TIMESTAMP(6)",
  })
  createdAt: Date;

  @UpdateDateColumn({
    type: "timestamp",
    default: () => "CURRENT_TIMESTAMP(6)",
    onUpdate: "CURRENT_TIMESTAMP(6)",
  })
  updatedAt: Date;
}
