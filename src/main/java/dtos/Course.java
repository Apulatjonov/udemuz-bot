package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Abdulaziz Pulatjonov
 * Date: 06/30/2023 15:17
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id;
    private String courseName;
    private String author;
    private String url;

    @Override
    public String toString(){
        return "Course name: " + this.courseName
                + "\n Author: " + this.author
                + "\nLink: " + this.url;
    }

    public static class CourseBuilder{
        private Long id;
        private String courseName;
        private String author;
        private String url;

        public CourseBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public CourseBuilder setCourseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public CourseBuilder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public CourseBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Course build(){
            return new Course(this.id, this.courseName, this.author, this.url);
        }
    }
}
