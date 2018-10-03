package net.mmm.survival.util.logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Import from abgie.tool.util.logging.Filter
 *
 * @author Abgie on 03.10.2018 17:01
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public final class Filter {
  private final List<String> filter;

  Filter() {
    this.filter = new ArrayList<>();
  }

  public void add(final String filter) {
    this.filter.add(filter);
  }

  public void remove(final String filter) {
    this.filter.remove(filter);
  }

  public void clear() {
    this.filter.clear();
  }

  public List<String> get() {
    return filter;
  }

}

