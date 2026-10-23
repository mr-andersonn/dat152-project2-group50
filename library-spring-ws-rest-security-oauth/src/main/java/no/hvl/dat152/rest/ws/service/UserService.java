/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import java.util.List;
import java.util.Set;

import no.hvl.dat152.rest.ws.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    public List<User> findAllUsers(){

        List<User> allUsers = (List<User>) userRepository.findAll();

        return allUsers;
    }

    public User findUser(Long userid) throws UserNotFoundException {

        User user = userRepository.findById(userid)
                .orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));

        return user;
    }



    public User saveUser(User user) {

        return userRepository.save(user);
    }


    public void deleteUser(Long id) throws UserNotFoundException{
        User user = findUser(id);
        userRepository.delete(user);
    }


    public User updateUser(User user, Long id) throws UserNotFoundException {
        User updated = findUser(id);

        updated.setFirstname(user.getFirstname());
        updated.setLastname(user.getLastname());

        return userRepository.save(updated);
    }


    public Set<Order> getUserOrders(Long userid) throws UserNotFoundException {
        User user = findUser(userid);
        return user.getOrders();
    }


    public Order getUserOrder(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException {
        User user = findUser(userid);

        for(Order order : user.getOrders()) {
            if(order.getId().equals(oid)){
                return order;
            }
        }

        throw new OrderNotFoundException("Order " + oid + " was not found");
    }

    public void deleteOrderForUser(Long userid, Long oid) throws UserNotFoundException, OrderNotFoundException {
        User user = findUser(userid);
        Order order = getUserOrder(userid, oid);

        user.removeOrder(order);
        userRepository.save(user);
        orderRepository.delete(order);
    }


    public User createOrdersForUser(Long userid, Order order) throws UserNotFoundException {
        User user = findUser(userid);

        user.addOrder(order);

        return userRepository.save(user);
    }
}
