package com.encore.byebuying.domain.basket.controller;

import com.encore.byebuying.config.auth.LoginUser;
import com.encore.byebuying.domain.basket.dto.CreateBasketItemDTO;
import com.encore.byebuying.domain.basket.dto.DeleteBasketItemListDTO;
import com.encore.byebuying.domain.basket.dto.SearchBasketItemListDTO;
import com.encore.byebuying.domain.basket.dto.UpdateBasketItemDTO;
import com.encore.byebuying.domain.basket.service.BasketService;
import com.encore.byebuying.domain.basket.service.vo.BasketItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/baskets")
@RequiredArgsConstructor
public class BasketController {
    private final BasketService basketService;

    // 유저별 장바구니 상품 조회
    @GetMapping("/by-user")
    public ResponseEntity<?> getBasketItemList(@AuthenticationPrincipal LoginUser loginUser, @Valid SearchBasketItemListDTO dto) {
        dto.setUserId(loginUser.getUserId());
        Page<BasketItemVO> basketItemsByUser = basketService.getBasketItemList(dto);
        return new ResponseEntity<>(basketItemsByUser,HttpStatus.OK);
    }

    // 장바구니 상품 추가
    @PostMapping(value = "/basket-item",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createBasketItem(@AuthenticationPrincipal LoginUser loginUser,@RequestBody @Valid CreateBasketItemDTO dto) {
        dto.setUserId(loginUser.getUserId());
        basketService.createBasketItem(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 장바구니 상품 갯수 수정
    @PutMapping(value = "/basket-items/{basket-item-id}/count" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateBasketItemCount(@AuthenticationPrincipal LoginUser loginUser,@RequestBody @Valid UpdateBasketItemDTO dto,
        @PathVariable(value = "basket-item-id") Long basketItemId) {
        dto.setUserId(loginUser.getUserId());
        basketService.updateBasketItemCount(dto, basketItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 장바구니 상품 삭제
    @DeleteMapping(value = "/basket-item" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteBasketItemList(@AuthenticationPrincipal LoginUser loginUser,@RequestBody @Valid DeleteBasketItemListDTO dto) {
        basketService.deleteBasketItemList(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
