package com.tyr.finance.stock.controller;

import com.tyr.finance.stock.service.rangebreak.RangeBreakService;
import com.tyr.finance.stock.vo.ResultVo;
import com.tyr.finance.stock.vo.rangebreak.CalculateParamVo;
import com.tyr.finance.stock.vo.rangebreak.CalculateResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TurtleController {
    private static final Logger logger = LoggerFactory.getLogger(TurtleController.class);

    @RequestMapping(value = "/turtle/newHigh/{count}", method = RequestMethod.GET)
    public ResultVo<List<CalculateResultVo>> newHigh(@PathVariable("count") Integer count) {
        ResultVo<List<CalculateResultVo>> resultVo = new ResultVo<>();
//        try {
//            resultVo.setStatus(200);
//            resultVo.setData(rangeBreakService.calculateByWeeklyDeals(param));
//        } catch (Exception e) {
//            resultVo.setStatus(500);
//            resultVo.setMessage(e.getMessage());
//        }
        return resultVo;
    }
}
