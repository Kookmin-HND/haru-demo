import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  CreateDateColumn,
  OneToMany,
  JoinTable,
  OneToOne,
  JoinColumn,
} from "typeorm";
import { Post } from "./post";
import { ProfileImageFile } from "./profileImageFile";

// user 스키마 생성

@Entity()
export class User {
  //id : auto Increment
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  email: string;

  @Column() // hashing된 비밀번호
  password: string;

  @Column()
  name: string;

  @CreateDateColumn()
  createAt: Date;


  //사진과 이미지 연결
  @OneToMany(() => ProfileImageFile, (image) => image.user, {
    cascade: true,
  })
  @JoinTable()
  images: ProfileImageFile[];


  //게시물과 사용자 연결
  @OneToMany(()=> Post, (post)=>post.user)
  @JoinTable()
  posts: Post[];

}
