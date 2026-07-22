package exolex.exotic.service;

import exolex.exotic.dtos.ClienteRequestDTO;
import exolex.exotic.dtos.ClienteResponseDTO;
import exolex.exotic.exception.ClienteNotFoundException;
import exolex.exotic.map.ClienteMapper;
import exolex.exotic.model.Cliente;
import exolex.exotic.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {
        Cliente cliente = clienteMapper.toEntity(dto);
        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    public Page<ClienteResponseDTO> listarTodos(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(clienteMapper::toResponseDTO);
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        return clienteMapper.toResponseDTO(cliente);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        clienteMapper.updateEntityFromDTO(dto, cliente);
        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    public void deletar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        clienteRepository.delete(cliente);
    }
}