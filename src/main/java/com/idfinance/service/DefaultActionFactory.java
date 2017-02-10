package com.idfinance.service;

import com.idfinance.domain.*;
import org.springframework.stereotype.Service;

@Service
public class DefaultActionFactory implements ActionFactory {

    @Override
    public Action getAction(ActionType type, Task task) {
        switch(type) {
            case print:
                return new Print(task);
            case random:
                return new Random(task);
        }
        throw new IllegalArgumentException("Not supported action type.");
    }
}
