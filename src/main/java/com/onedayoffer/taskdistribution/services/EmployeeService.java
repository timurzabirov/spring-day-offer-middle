package com.onedayoffer.taskdistribution.services;

import ch.qos.logback.classic.Logger;
import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.repositories.EmployeeRepository;
import com.onedayoffer.taskdistribution.repositories.TaskRepository;
import com.onedayoffer.taskdistribution.repositories.entities.Employee;
import com.onedayoffer.taskdistribution.repositories.entities.Task;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public List<EmployeeDTO> getEmployees(@Nullable String sortDirection) {
        if (sortDirection == null) {
            return employeeRepository.findAll().stream().map(employee -> modelMapper.map(employee, EmployeeDTO.class)).toList();
        } else {
            return employeeRepository.findAll(Sort.by(sortDirection)).stream().map(employee -> modelMapper.map(employee, EmployeeDTO.class)).toList();
        }
    }

    @Transactional
    public EmployeeDTO getOneEmployee(Integer id) {
        var employee = employeeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    public List<TaskDTO> getTasksByEmployeeId(Integer id) {
        return taskRepository.findAllByEmployeeId(id).stream().map(task -> modelMapper.map(task, TaskDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public void changeTaskStatus(Integer taskId, TaskStatus status) {
        var task = taskRepository.findById(taskId).orElseThrow(EntityNotFoundException::new);
        task.setStatus(status);
        taskRepository.save(task);
    }

    @Transactional
    public void postNewTask(Integer employeeId, TaskDTO newTask) {
        var employee = employeeRepository.findById(employeeId).orElseThrow(EntityNotFoundException::new);
        var task = new Task();
        task.setName(newTask.getName());
        task.setStatus(newTask.getStatus());
        task.setTaskType(newTask.getTaskType());
        task.setLeadTime(newTask.getLeadTime());
        task.setPriority(newTask.getPriority());
        task.setEmployee(employee);
        taskRepository.save(task);
    }
}
