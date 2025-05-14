package org.cc.model.col;



public interface ICCColumn {
    
    default String asString(int idx){
        return "";
    }
}
