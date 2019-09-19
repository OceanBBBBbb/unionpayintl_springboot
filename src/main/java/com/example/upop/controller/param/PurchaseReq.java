package com.example.upop.controller.param;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author binhy
 * @desc TODO
 * @date 2019/9/18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReq {

  @Ignore
  private String merId = "709034445110001";
  @Ignore
  private String IIN = "47090344";

  private String orderId;
  private String txnTime;
  private String txnAmt="10";
}
