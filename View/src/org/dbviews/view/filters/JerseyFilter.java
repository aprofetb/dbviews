package org.dbviews.view.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class JerseyFilter
  implements ContainerRequestFilter
{
  public ContainerRequest filter(ContainerRequest request)
  {
    return request;
  }
}
