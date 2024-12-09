package com.msa.order.application.service;

import com.msa.order.application.service.dto.CreateDeliveryRequest;
import com.msa.order.application.service.dto.CreateDeliveryResponse;
import com.msa.order.application.service.dto.ProductStockRequest;
import com.msa.order.application.service.dto.ProductStockResponse;
import com.msa.order.domain.entity.Address;
import com.msa.order.domain.entity.Order;
import com.msa.order.domain.repository.OrderRepository;
import com.msa.order.exception.BusinessException.OrderException;
import com.msa.order.exception.ErrorCode;
import com.msa.order.presentation.request.CreateOrderRequest;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CreateOrderService {

  private final OrderRepository orderRepository;
  private final ProductManager productManager;
  private final DeliveryManager deliveryManager;

  public CreateOrderService(OrderRepository orderRepository,
      @Qualifier("productClient") ProductManager productManager,
      @Qualifier("deliveryClient") DeliveryManager deliveryManager) {
    this.orderRepository = orderRepository;
    this.productManager = productManager;
    this.deliveryManager = deliveryManager;
  }

  // TODO 유저,상품,배송 서비스 연동 전 임시 하드 코딩
  final UUID receiverCompanyId = UUID.randomUUID();
  final String receiverName = "test receiver";
  final String receiverSlackId = "test slack id";
  final UUID itemId =  UUID.randomUUID();
  final UUID hubId =  UUID.randomUUID();
  final UUID deliveryId =  UUID.randomUUID();

  @Transactional
  public void createOrder(CreateOrderRequest request) {
    ProductStockResponse productStockResponse = reduceProductStock(request);
    Order savedOrder = createAndSaveOrder(request);
    CreateDeliveryRequest deliveryRequest = createDeliveryRequest(request, productStockResponse, savedOrder);
    createDeliveryAndUpdateOrder(deliveryRequest, savedOrder);
  }

  private void createDeliveryAndUpdateOrder(CreateDeliveryRequest deliveryRequest, Order savedOrder) {
    // Todo 배송 서비스 연동 테스트 필요
//    CreateDeliveryResponse deliveryResponse = deliveryManager.createDelivery(deliveryRequest);
//    if(deliveryResponse.deliveryId() == null) {
//      throw new OrderException(ErrorCode.REQUEST_DELIVERY_FAILED);
//    }
    CreateDeliveryResponse deliveryResponse = new CreateDeliveryResponse(deliveryId);
    savedOrder.updateDeliveryId(deliveryResponse.deliveryId());
  }

  private CreateDeliveryRequest createDeliveryRequest(CreateOrderRequest request, ProductStockResponse productStockResponse, Order savedOrder) {

    Address address = Address.of(request.city(), request.district(), request.streetName(),
        request.streetNum(), request.detail());

    return new CreateDeliveryRequest(savedOrder.getId(),
        receiverCompanyId, receiverName, receiverSlackId, address, request.supplierCompanyId(),
        productStockResponse.hubId());
  }

  private Order createAndSaveOrder(CreateOrderRequest request) {
    Order order = Order.create(request.supplierCompanyId(), receiverCompanyId, request.itemId(),
        request.quantity(), request.description());

    return orderRepository.save(order);
  }

  private ProductStockResponse reduceProductStock(CreateOrderRequest request) {
   // Todo 상품 서비스 연동 테스트 필요
//    ProductStockResponse response = productManager.reduceStock(request.itemId(),
//        new ProductStockRequest(request.quantity()));
//
//    if (response.id() == null) {
//      throw new OrderException(ErrorCode.STOCK_REDUCTION_FAILED);
//    }
//
//    return response;
    return new ProductStockResponse(itemId, hubId);
  }

}