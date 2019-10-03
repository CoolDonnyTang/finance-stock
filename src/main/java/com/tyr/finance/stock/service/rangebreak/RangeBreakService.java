package com.tyr.finance.stock.service.rangebreak;

import com.tyr.finance.stock.vo.rangebreak.CalculateParamVo;
import com.tyr.finance.stock.vo.rangebreak.CalculateResultVo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RangeBreakService {

    List<CalculateResultVo> calculateByWeeklyDeals(CalculateParamVo param) throws Exception;
}
