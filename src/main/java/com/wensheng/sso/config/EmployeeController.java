package com.wensheng.sso.config;


import com.wensheng.sso.model.Employee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class EmployeeController {

    private List<Employee> employees = new ArrayList<>();

    @GetMapping("/employee")
    @ResponseBody
    public Optional<Employee> getEmployee(@RequestParam String email) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();

            System.out.println(username);
        } else {
            String username = principal.toString();
            System.out.println(username);
        }
        return employees.stream().filter(x -> x.getEmail().equals(email)).findAny();
    }

    @PostMapping(value = "/employee", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void postMessage(@RequestBody Employee employee) {
        if(employee.getName() == null || employee.getEmail() == null){
            throw new IllegalArgumentException(String.format("Wrong parameter for create employee with name:%s, "
                + "email:%s", employee.getName(), employee.getEmail()));
        }
        employees.add(employee);
    }

}
