/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UnauthorizedOrderActionException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(){

        List<User> users = userService.findAllUsers();

        if(users.isEmpty())

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.details.userid")
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) throws UserNotFoundException, OrderNotFoundException{

        User user = userService.findUser(id);

        return new ResponseEntity<>(user, HttpStatus.OK);

    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newuser = userService.saveUser(user);
        return new ResponseEntity<>(newuser, HttpStatus.CREATED);
    }


    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.details.userid")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) throws UserNotFoundException {
        User updated = userService.updateUser(user, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.details.userid")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.details.userid")
    @GetMapping("/users/{id}/orders")
    public ResponseEntity<Object> getUserOrders(@PathVariable Long id) throws UserNotFoundException {
        Set<Order> orders = userService.getUserOrders(id);

        if (orders.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or #uid == authentication.details.userid")
    @GetMapping("/users/{uid}/orders/{oid}")
    public ResponseEntity<Order> getUserOrder(@PathVariable Long uid, @PathVariable Long oid) throws UserNotFoundException, OrderNotFoundException {
        Order order = userService.getUserOrder(uid,oid);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or #uid == authentication.details.userid")
    @DeleteMapping("/users/{uid}/orders/{oid}")
    public ResponseEntity<Order> deleteUserOrder(@PathVariable Long uid, @PathVariable Long oid) throws UserNotFoundException, OrderNotFoundException {
        userService.deleteOrderForUser(uid, oid);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or #id == authentication.details.userid")
    @PostMapping("/users/{id}/orders")
    public ResponseEntity<Object> createUserOrder(@PathVariable Long id, @RequestBody Order order) throws UserNotFoundException, OrderNotFoundException {
        User user = userService.createOrdersForUser(id, order);

        Set<Order> orders = user.getOrders();
        addLinks(orders);

        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    private void addLinks(Set<Order> orders) throws OrderNotFoundException {
        for (Order order : orders) {
            Link link = linkTo(methodOn(OrderController.class)
                    .getBorrowOrder(order.getId()))
                    .withRel("Update_Return_or_Cancel");
            order.add(link);
        }
    }
}
