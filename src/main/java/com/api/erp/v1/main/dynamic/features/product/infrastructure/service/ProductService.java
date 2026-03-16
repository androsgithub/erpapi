package com.api.erp.v1.main.dynamic.features.product.infrastructure.service;

import com.api.erp.v1.main.dynamic.features.customfield.domain.entity.CustomData;
import com.api.erp.v1.main.dynamic.features.customfield.domain.entity.CustomFieldDefinition;
import com.api.erp.v1.main.dynamic.features.product.application.dto.ClassificacaoFiscalDTO;
import com.api.erp.v1.main.dynamic.features.product.application.dto.ProductRequestDTO;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.ClassificacaoFiscal;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.Product;
import com.api.erp.v1.main.dynamic.features.product.domain.entity.ProductType;
import com.api.erp.v1.main.dynamic.features.product.domain.exception.ProductException;
import com.api.erp.v1.main.dynamic.features.product.domain.repository.ProductRepository;
import com.api.erp.v1.main.dynamic.features.product.domain.service.IProductService;
import com.api.erp.v1.main.dynamic.features.product.domain.validator.ProductValidator;
import com.api.erp.v1.main.dynamic.features.measureunit.domain.entity.MeasureUnit;
import com.api.erp.v1.main.dynamic.features.measureunit.domain.repository.MeasureUnitRepository;
import com.api.erp.v1.main.shared.domain.exception.BusinessException;
import com.api.erp.v1.main.shared.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de Aplicação para Product
 * <p>
 * Responsibilities:
 * - Orquestrar operações de domínio
 * - Coordenar transações
 * - Transformar DTOs
 * <p>
 * SRP: Lógica de aplicação para Product
 * DIP: Depende de abstrações (repositórios, validadores)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService implements IProductService {

    private final ProductRepository repository;
    private final MeasureUnitRepository measureUnitRepository;
    private final ProductValidator validator;

    public Product criar(ProductRequestDTO dto) {
        validator.validarCriacao(dto.getCodigo(), dto.getDescricao());

        if (repository.existsByCodigo(dto.getCodigo())) {
            throw ProductException.codigoJaExiste(dto.getCodigo());
        }

        Product product = new Product();
        product.setCodigo(dto.getCodigo());
        product.setDescricao(dto.getDescricao());
        product.setStatus(dto.getStatus());
        product.setType(dto.getTipo());

        MeasureUnit measureUnit = obterMeasureUnit(dto.getMeasureUnitId());
        product.setMeasureUnit(measureUnit);
        product.setClassificacaoFiscal(criarClassificacaoFiscal(dto.getClassificacaoFiscal()));
        product.setPrecoVenda(dto.getPrecoVenda());
        product.setPrecoCusto(dto.getPrecoCusto());
        product.setDescricaoDetalhada(dto.getDescricaoDetalhada());

        List<CustomData> listCustomData = List.of();

        dto.getCustomData().forEach((key,value )-> {
            CustomData cd = new CustomData();
            CustomFieldDefinition cfd = new CustomFieldDefinition();
            cfd.setFieldKey(key);
            cd.setField(cfd);
            listCustomData.add(cd);
        });

        product.setCustomData(listCustomData);


        return repository.save(product);
    }

    public Product atualizar(Long id, ProductRequestDTO productModificado) {
        Product product = obterPorId(id);

        // Se o código foi alterado, validar unicidade
        if (!product.getCodigo().equals(productModificado.getCodigo())) {
            if (repository.existsByCodigo(productModificado.getCodigo())) {
                throw ProductException.codigoJaExiste(productModificado.getCodigo());
            }
        }

        validator.validarCriacao(productModificado.getCodigo(), productModificado.getDescricao());
        return repository.save(product);
    }

    /**
     * Gets um product por ID
     */
    @Transactional(readOnly = true)
    public Product obter(Long id) {
        return obterPorId(id);
    }

    /**
     * Lista todos os products (paginado)
     */
    @Transactional(readOnly = true)
    public Page<Product> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Lista products por tipo
     */
    @Transactional(readOnly = true)
    public Page<Product> listarPorTipo(ProductType type, Pageable pageable) {
        return repository.findByType(type).stream()
                .reduce(
                        Page.empty(pageable),
                        (page, dto) -> page,
                        (p1, p2) -> p1
                );
    }

    /**
     * Ativa um product
     */
    public Product ativar(Long id) {
        Product product = obterPorId(id);
        product.ativar();
        return repository.save(product);
    }

    /**
     * Desativa um product
     */
    public Product desativar(Long id) {
        Product product = obterPorId(id);
        product.desativar();
        return repository.save(product);
    }

    /**
     * Bloqueia um product
     */
    public Product bloquear(Long id) {
        Product product = obterPorId(id);
        product.bloquear();
        return repository.save(product);
    }

    /**
     * Descontinua um product
     */
    public Product descontinuar(Long id) {
        Product product = obterPorId(id);
        product.descontinuar();
        return repository.save(product);
    }

    /**
     * Deleta um product
     */
    public void deletar(Long id) {
        obterPorId(id); // Valida existência
        repository.deleteById(id);
    }

    /**
     * Gets um product por ID ou lança exceção
     */
    private Product obterPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ProductException.productNaoEncontrado(id));
    }

    /**
     * Gets unidade de medida ou lança exceção
     */
    private MeasureUnit obterMeasureUnit(Long id) {
        return measureUnitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "Unidade de medida não encontrada com ID: " + id
                ));
    }

    /**
     * Cria uma ClassificacaoFiscal a partir do DTO
     */
    private ClassificacaoFiscal criarClassificacaoFiscal(ClassificacaoFiscalDTO dto) {
        NCM ncm = NCM.de(dto.getNcm());
        OrigemMercadoria origem = OrigemMercadoria.doCodigo(dto.getOrigemMercadoria());
        CodigoBeneficioFiscal beneficio = dto.getCodigoBeneficioFiscal() != null
                ? CodigoBeneficioFiscal.de(dto.getCodigoBeneficioFiscal())
                : null;
        CEST cest = dto.getCest() != null
                ? CEST.de(dto.getCest())
                : null;
        UnidadeTributavel unidadeTributavel = UnidadeTributavel.de(
                dto.getUnidadeTributavelCodigo(),
                dto.getUnidadeTributavelDescricao()
        );

        return ClassificacaoFiscal.criar(
                ncm,
                dto.getDescricaoFiscal(),
                origem,
                beneficio,
                cest,
                unidadeTributavel
        );
    }

}
