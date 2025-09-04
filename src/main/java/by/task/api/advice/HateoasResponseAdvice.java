package by.task.api.advice;

import by.task.api.assembler.UserModelAssembler;
import by.task.dto.UserResponseDTO;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class HateoasResponseAdvice implements ResponseBodyAdvice<Object> {

    private final UserModelAssembler userAssembler;

    public HateoasResponseAdvice(UserModelAssembler userAssembler) {
        this.userAssembler = userAssembler;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof UserResponseDTO user) {
            return userAssembler.toModel(user);
        }

        if (body instanceof List<?> list && !list.isEmpty() && list.getFirst() instanceof UserResponseDTO) {
            return list.stream()
                    .map(user -> userAssembler.toModel((UserResponseDTO) user))
                    .collect(Collectors.toList());
        }

        return body;
    }
}