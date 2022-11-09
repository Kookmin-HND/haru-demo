import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  Generated,
  UpdateDateColumn,
  OneToMany,
  JoinTable,
} from "typeorm";
import { Comment } from "./comment";
import { imageFile } from "./imageFile";

//필요한 데이터베이스 스키마 entity에 생성

@Entity()
export class Post {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ nullable: false })
  writer: string;

  @Column({ nullable: false })
  title: string;

  @Column({ nullable: false })
  content: string;

  //댓글과 일대다 연결, 게시물이 삭제되면 댓글도 삭제되도록 cascade
  @OneToMany(() => Comment, (comment) => comment.post, {
    cascade: true,
  })
  @JoinTable()
  comments: Comment[];

  //이미지와 일대다 연결, 게시물이 삭제되면 이미지도 삭제되도록 cascade
  @OneToMany(() => imageFile, (imagefile) => imagefile.post, {
    cascade: true,
  })
  @JoinTable()
  imageFiles: imageFile[];

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
