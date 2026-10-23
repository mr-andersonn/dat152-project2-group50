/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

	// TODO - getAllBorrowOrders (@Mappings, URI=/orders, and method) + filter by expiry and paginate
    @GetMapping("/orders")
    public ResponseEntity<Object> getAllBorrowOrders(@RequestParam(value = "expiry", required = false) String expiry,
                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value="size", defaultValue = "10") int size){
        List<Order> orders;

        if(expiry != null){
            LocalDate date = LocalDate.parse(expiry);
            orders = orderService.findByExpiryDate(date, PageRequest.of(page, size));
        }
        else{
            orders = orderService.findAllOrders();
        }

        if(orders.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
	
	// TODO - getBorrowOrder (@Mappings, URI=/orders/{id}, and method)
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getBorrowOrder(@PathVariable Long id) throws OrderNotFoundException {
        Order order = orderService.findOrder(id);

        Link link = linkTo(methodOn(OrderController.class)
                .getBorrowOrder(order.getId()))
                .withRel("Update_Return_or_Cancel");
        order.add(link);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
	
	// TODO - updateOrder (@Mappings, URI=/orders/{id}, and method)
    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) throws OrderNotFoundException {
        Order updated = orderService.updateOrder(order, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
	
	// TODO - deleteBookOrder (@Mappings, URI=/orders/{id}, and method)
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Order> deleteBookOrder(@PathVariable Long id) throws OrderNotFoundException {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
	
}
