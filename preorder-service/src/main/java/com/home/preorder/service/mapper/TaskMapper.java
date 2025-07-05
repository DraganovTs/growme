package com.home.preorder.service.mapper;

import com.home.preorder.service.model.dto.TaskDTO;
import com.home.preorder.service.model.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task mapTaskDTOToTask(TaskDTO taskDTO) {
        return Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .userId(taskDTO.getUserId())
                .budget(taskDTO.getBudget())
                .deadline(taskDTO.getDeadline())
                .quality(taskDTO.getQuality())
                .unit(taskDTO.getUnit())
                .quantity(taskDTO.getQuantity())
                .harvestDate(taskDTO.getHarvestDate())
                .deliveryDate(taskDTO.getDeliveryDate())
                .flexibleDates(taskDTO.isFlexibleDates())
                .deliveryLocation(taskDTO.getDeliveryLocation())
                .deliveryMethod(taskDTO.getDeliveryMethod())
                .willingToShip(taskDTO.isWillingToShip())
                .priceModel(taskDTO.getPriceModel())
                .photosRequired(taskDTO.isPhotosRequired())
                .visitFarm(taskDTO.isVisitFarm())
                .build();
    }

    public TaskDTO mapTaskToTaskDTO(Task save) {
        TaskDTO mapped = TaskDTO.builder()
                .taskId(save.getTaskId())
                .title(save.getTitle())
                .description(save.getDescription())
                .categoryName(save.getCategory().getCategoryName())
                .status(save.getStatus())
                .userId(save.getUserId())
                .budget(save.getBudget())
                .deadline(save.getDeadline())
                .quality(save.getQuality())
                .unit(save.getUnit())
                .quantity(save.getQuantity())
                .harvestDate(save.getHarvestDate())
                .deliveryDate(save.getDeliveryDate())
                .flexibleDates(save.isFlexibleDates())
                .deliveryLocation(save.getDeliveryLocation())
                .deliveryMethod(save.getDeliveryMethod())
                .willingToShip(save.isWillingToShip())
                .priceModel(save.getPriceModel())
                .photosRequired(save.isPhotosRequired())
                .visitFarm(save.isVisitFarm())
                .build();

    return mapped;
    }

}
