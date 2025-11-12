
package com.mpt.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="student_id", nullable=false, unique=true)
    private Student student;

    @Email @Size(max=120)
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9\\- ]{7,20}$",
            message = "Телефон: только цифры, пробелы и дефис; можно начать с +; длина 7–20 символов"
    )
    private String phone;

    @Size(max=255)
    private String address;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
