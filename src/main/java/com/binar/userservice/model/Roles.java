package com.binar.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "role_name", columnNames = "role_name")
        })
public class Roles{

    @Id
    @GeneratedValue(generator = "user-generator")
    @GenericGenerator(name = "user-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "user_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            })
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", length = 256, nullable = false)
    private String roleName;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "rolesUsers", cascade = CascadeType.ALL)
    private List<Users> users = new ArrayList<>();
}