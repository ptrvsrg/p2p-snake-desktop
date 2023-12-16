package ru.nsu.ccfit.network.p2psnake.desktop.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HubService {

    private final NodeStorage nodeStorage;

    public List<String> getNodeList() {
        return nodeStorage.getNodeList();
    }
}
