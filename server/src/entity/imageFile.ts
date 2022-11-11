import { Entity, Column, PrimaryGeneratedColumn, CreateDateColumn, Generated, UpdateDateColumn, ManyToOne, JoinColumn, BaseEntity } from "typeorm";
import { Post } from "./post";

//필요한 데이터베이스 스키마 entity에 생성
@Entity()
export class ImageFile {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  //게시물이 삭제되면 이미지도 삭제되도록 cascade
  @ManyToOne(() => Post, (post) => post.imageFiles, {
    onDelete: "CASCADE",
    nullable: false,
  })
  @JoinColumn()
  post: Post;

  @Column({ nullable: false })
  url: string;

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
