package it.isgroup.identity.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import it.isgroup.identity.rest.UserController;
import it.isgroup.identity.rest.dto.UserResponse;

/**
 * Assembler HATEOAS per convertire un {@link UserResponse} in {@link EntityModel}
 * arricchito con link HAL (self, collection, update, delete).
 * Hypermedia as the Engine of Application State
 */
@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

  @Override
  public EntityModel<UserResponse> toModel(UserResponse user) {
    return EntityModel.of(
        user,
        linkTo(methodOn(UserController.class).get(user.id())).withSelfRel(),
        linkTo(methodOn(UserController.class).list(null)).withRel("users"),
        linkTo(methodOn(UserController.class).update(user.id(), null)).withRel("update"),
        linkTo(methodOn(UserController.class).delete(user.id())).withRel("delete")
    );
  }
}
