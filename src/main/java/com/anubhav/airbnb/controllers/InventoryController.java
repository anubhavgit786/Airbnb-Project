package com.anubhav.airbnb.controllers;

import com.anubhav.airbnb.dtos.InventoryResponseDto;
import com.anubhav.airbnb.dtos.UpdateInventoryRequestDto;
import com.anubhav.airbnb.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController
{
    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Get all inventory of a room", tags = {"Admin Inventory"})
    public ResponseEntity<List<InventoryResponseDto>> getAllInventoryByRoom(@PathVariable Long roomId)
    {
        List<InventoryResponseDto> response = inventoryService.getAllInventoryByRoom(roomId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/rooms/{roomId}")
    @Operation(summary = "Update the inventory of a room", tags = {"Admin Inventory"})
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId, @Valid
                                                @RequestBody UpdateInventoryRequestDto request)
    {
        inventoryService.updateInventory(roomId, request);
        return ResponseEntity.noContent().build();
    }
}
