package com.msa.order.application.service;

import com.msa.order.application.client.DeliveryManager;
import com.msa.order.application.client.ProductManager;
import com.msa.order.application.client.dto.CreateDeliveryRequest;
import com.msa.order.application.client.dto.DeliveryData;
import com.msa.order.application.client.dto.ProductStockData;
import com.msa.order.domain.entity.Order;
import com.msa.order.domain.repository.OrderRepository;
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
  final String receiverName = "test receiver";
  final String receiverSlackId = "test slack id";
  final UUID itemId =  UUID.randomUUID();
  final UUID hubId =  UUID.randomUUID();
  final UUID deliveryId =  UUID.randomUUID();

  @Transactional
  public void createOrder(CreateOrderRequest request) {
    ProductStockData productStockData = reduceProductStock(request);
    Order savedOrder = createAndSaveOrder(request, productStockData.hubId());
    CreateDeliveryRequest deliveryRequest = createDeliveryRequest(request, productStockData, savedOrder);
    createDeliveryAndUpdateOrder(deliveryRequest, savedOrder);
  }

  private void createDeliveryAndUpdateOrder(CreateDeliveryRequest deliveryRequest, Order savedOrder) {
  //  Todo 배송 서비스 연동 테스트 필요
  //   DeliveryData deliveryResponse = deliveryManager.createDelivery(deliveryRequest);
  //  if(deliveryResponse.deliveryId() == null) {
  //    throw new OrderException(ErrorCode.REQUEST_DELIVERY_FAILED);
  //  }
    DeliveryData deliveryResponse = new DeliveryData(1L, null,null,null, null, null, null, null, null);
    savedOrder.updateDeliveryId(deliveryResponse.deliveryId());
  }

  private CreateDeliveryRequest createDeliveryRequest(CreateOrderRequest request, ProductStockData productStockData, Order savedOrder) {
    //TODO header에 role MASTER 넣어서 요청보내기
   // CompanyData receiveCompany = productManager.getCompanyInfo(request.receiveCompanyId());

    // return new CreateDeliveryRequest(savedOrder.getId(), receiverName, receiverSlackId, savedOrder.getAddress(),
    //     request.supplierCompanyId(), productStockData.hubId(), receiveCompany.hubId());
    return new CreateDeliveryRequest(savedOrder.getId(), receiverName, receiverSlackId, savedOrder.getAddress(),
        request.supplierCompanyId(), productStockData.hubId(), hubId);
  }

  private Order createAndSaveOrder(CreateOrderRequest request, UUID departureHubId) {

    Order order = Order.create(request.supplierCompanyId(), request.receiveCompanyId(),
        request.itemId(), request.itemName(), request.quantity(), request.description(),
        request.city(), request.district(), request.streetName(), request.streetNum(), request.detail(), departureHubId);

    return orderRepository.save(order);
  }

  private ProductStockData reduceProductStock(CreateOrderRequest request) {
   // Todo 상품 서비스 연동 테스트 필요
   // ProductStockData response = productManager.reduceStock(request.itemId(),
   //     new ProductStockRequest(request.quantity()));
   //
   // if (response.id() == null) {
   //   throw new OrderException(ErrorCode.STOCK_REDUCTION_FAILED);
   // }
   //
   // return response;
    return new ProductStockData(itemId, hubId);
  }

}