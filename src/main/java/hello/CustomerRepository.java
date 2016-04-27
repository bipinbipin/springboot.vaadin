package hello;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by bipin on 4/26/16.
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {


    List<Customer> findByLastNameStartsWithIgnoreCase(String lastname);
}
