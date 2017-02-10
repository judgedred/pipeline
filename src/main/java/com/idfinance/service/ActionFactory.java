package com.idfinance.service;

import com.idfinance.domain.Action;
import com.idfinance.domain.ActionType;
import com.idfinance.domain.Task;

public interface ActionFactory {
    Action getAction(ActionType type, Task task);
}
