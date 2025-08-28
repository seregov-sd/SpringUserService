package by.task.api.assembler;

import by.task.controller.UserController;
import by.task.dto.UserResponseDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDTO, EntityModel<UserResponseDTO>> {

    private static final String ALL_USERS = "all-users";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    @Override
    @NotNull
    public EntityModel<UserResponseDTO> toModel(UserResponseDTO user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel(ALL_USERS),
                linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel(UPDATE),
                linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel(DELETE)
        );
    }
}