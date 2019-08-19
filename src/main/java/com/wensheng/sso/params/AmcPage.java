package com.wensheng.sso.params;

import java.util.List;
import lombok.Data;

/**
 * @author chenwei on 3/13/19
 * @project zcc-backend
 */
@Data
public class AmcPage<T> {
  List<T> content;
  Long total;
}
