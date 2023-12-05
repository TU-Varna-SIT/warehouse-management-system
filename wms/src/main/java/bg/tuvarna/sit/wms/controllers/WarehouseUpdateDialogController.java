package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import bg.tuvarna.sit.wms.service.WarehouseService;
import bg.tuvarna.sit.wms.util.DialogUtil;

public class WarehouseUpdateDialogController extends BaseWarehouseDialogController {

  private final WarehouseService warehouseService = new WarehouseService();
  private WarehouseDTO warehouseDTO;

  public WarehouseUpdateDialogController(Owner owner, WarehouseDTO warehouseDTO) {
    super(owner);
    this.warehouseDTO = warehouseDTO;
  }

  @Override
  public void initialize() {
    super.initialize();
    initializeFields(warehouseDTO);
  }

  @Override
  public void onSave() {

    WarehouseDTO updatedWarehouseDTO = createWarehouseDTO();
    updatedWarehouseDTO.setId(warehouseDTO.getId());
    updatedWarehouseDTO.setStatus(warehouseDTO.getStatus());
    updatedWarehouseDTO.setOwner(warehouseDTO.getOwner());

    if(!validate()){
      return;
    }

    try {
      warehouseService.updateWarehouse(updatedWarehouseDTO);
    } catch (WarehouseServiceException e) {
      DialogUtil.showErrorAlert("Unable to update warehouse data", e.getMessage());
    }
    getDialogStage().close();
  }
}
