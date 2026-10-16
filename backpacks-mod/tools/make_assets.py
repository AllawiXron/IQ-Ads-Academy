"""Generates the Backpacks textures and models.

python3 tools/make_assets.py <folder with the old 1.19.2 item sprites>

- inventory icons: the original pouch sprites from Backpacks V6 (leather is turned grayscale so it can be dyed)
- 3D models + 64x64 textures for hand, ground and on-your-back rendering
- GUI backgrounds sized for each tier
"""
import json
import os
import random
import sys

from PIL import Image, ImageDraw

import icons

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'src', 'main', 'resources', 'assets', 'backpack')
TEX = os.path.join(ROOT, 'textures')
OLD = sys.argv[1] if len(sys.argv) > 1 else '.'

LEATHER_DEFAULT = 0xA06540  # same brown as vanilla leather armor

TIERS = {
    # name: (old sprite, base, dark, light, strap, metal, cols, rows)
    'leather': ('c90aa53d-9adc-4801-83ed-59577f023e35.png', (139, 90, 52), (96, 58, 30), (170, 118, 74), (78, 48, 26), (196, 196, 196), 9, 3),
    'iron': ('c90aa53d-9adc-4801-83ed-59577f023e35_1.png', (200, 200, 200), (140, 140, 146), (236, 236, 236), (90, 62, 40), (110, 110, 118), 9, 4),
    'golden': ('c90aa53d-9adc-4801-83ed-59577f023e35_2.png', (240, 196, 60), (190, 130, 20), (255, 236, 130), (110, 70, 34), (255, 250, 200), 9, 5),
    'diamond': ('c90aa53d-9adc-4801-83ed-59577f023e35_3.png', (78, 220, 206), (28, 150, 150), (180, 255, 245), (60, 70, 90), (230, 255, 255), 9, 6),
    'emerald': ('c90aa53d-9adc-4801-83ed-59577f023e35_4.png', (32, 196, 90), (12, 124, 52), (130, 250, 160), (70, 56, 40), (220, 255, 230), 11, 7),
    'netherite': ('c90aa53d-9adc-4801-83ed-59577f023e35_5.png', (74, 68, 74), (44, 40, 46), (110, 102, 108), (34, 30, 34), (150, 120, 110), 13, 8),
    'ender': (None, (24, 64, 66), (12, 36, 40), (52, 110, 108), (34, 18, 50), (180, 90, 220), 9, 3),
}

# --- 3D model -------------------------------------------------------------------------------------
# UV space is 16x16 over a 64x64 texture, faces use half-size UVs (1 model unit = 2 texels)
REGIONS = {
    'body_front': (0, 0, 5, 6), 'body_back': (5, 0, 10, 6), 'body_side': (10, 0, 13, 6), 'body_top': (0, 6, 5, 9),
    'flap_front': (0, 9, 5.5, 10.5), 'flap_top': (0, 10.5, 5.5, 14), 'flap_side': (5.5, 9, 9, 10.5),
    'pocket_front': (11, 6, 15, 9), 'pocket_edge': (11, 9, 15, 9.75),
    'strap': (12.5, 10.5, 13.25, 15.5), 'metal': (13.5, 10.5, 14.5, 11.5),
}
TINTED = {'body_front', 'body_back', 'body_side', 'body_top', 'flap_front', 'flap_top', 'flap_side', 'pocket_front', 'pocket_edge'}


def box(frm, to, faces, tinted):
    out = {'from': frm, 'to': to, 'faces': {}}
    for face, region in faces.items():
        f = {'uv': list(REGIONS[region]), 'texture': '#tex'}
        if tinted and region in TINTED:
            f['tintindex'] = 0
        out['faces'][face] = f
    return out


def model_3d(name, tinted):
    all6 = lambda front, back, side, top: {'north': front, 'south': back, 'east': side, 'west': side, 'up': top, 'down': top}
    elements = [
        box([3, 0, 5], [13, 11, 11], all6('body_front', 'body_back', 'body_side', 'body_top'), tinted),
        box([2.5, 10, 4.5], [13.5, 13, 11.5], {'north': 'flap_front', 'south': 'flap_front', 'east': 'flap_side', 'west': 'flap_side', 'up': 'flap_top', 'down': 'flap_top'}, tinted),
        box([4, 1.5, 3.5], [12, 7.5, 5], {'north': 'pocket_front', 'east': 'pocket_edge', 'west': 'pocket_edge', 'up': 'pocket_edge', 'down': 'pocket_edge'}, tinted),
        box([7, 8.5, 4], [9, 10.5, 4.5], {'north': 'metal', 'east': 'metal', 'west': 'metal', 'up': 'metal', 'down': 'metal'}, tinted),
        box([7.25, 5.5, 3], [8.75, 6.5, 3.5], {'north': 'metal', 'east': 'metal', 'west': 'metal', 'up': 'metal', 'down': 'metal'}, tinted),
        box([4.5, 0.5, 11], [6, 12, 12], {'south': 'strap', 'east': 'strap', 'west': 'strap', 'up': 'strap', 'down': 'strap'}, tinted),
        box([10, 0.5, 11], [11.5, 12, 12], {'south': 'strap', 'east': 'strap', 'west': 'strap', 'up': 'strap', 'down': 'strap'}, tinted),
    ]
    return {
        'texture_size': [64, 64],
        'textures': {'tex': 'backpack:item/' + name + '_backpack_3d', 'particle': 'backpack:item/' + name + '_backpack_3d'},
        'elements': elements,
        'display': {
            'thirdperson_righthand': {'rotation': [0, 180, 0], 'translation': [0, 2, -3], 'scale': [0.45, 0.45, 0.45]},
            'thirdperson_lefthand': {'rotation': [0, 180, 0], 'translation': [0, 2, -3], 'scale': [0.45, 0.45, 0.45]},
            'firstperson_righthand': {'rotation': [10, 200, 0], 'translation': [0, 5, 0], 'scale': [0.3, 0.3, 0.3]},
            'firstperson_lefthand': {'rotation': [10, 160, 0], 'translation': [0, 5, 0], 'scale': [0.3, 0.3, 0.3]},
            'ground': {'rotation': [0, 0, 0], 'translation': [0, 2, 0], 'scale': [0.45, 0.45, 0.45]},
            'fixed': {'rotation': [0, 180, 0], 'translation': [0, 0, 0], 'scale': [0.7, 0.7, 0.7]},
            'head': {'rotation': [0, 0, 0], 'translation': [0, 0, 0], 'scale': [1, 1, 1]},
            'gui': {'rotation': [25, 200, 0], 'translation': [0, 0, 0], 'scale': [0.7, 0.7, 0.7]},
        },
    }


def jitter(c, rnd, amt=8):
    k = rnd.randint(-amt, amt)
    return tuple(max(0, min(255, v + k)) for v in c)


def gray(c):
    return int(0.3 * c[0] + 0.59 * c[1] + 0.11 * c[2])


def texture_3d(name, base, dark, light, strap, metal, tinted):
    rnd = random.Random(hash(name) & 0xffff)
    img = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    px = img.load()

    def to_tint(c):
        if not tinted:
            return c
        g = min(255, int(gray(c) * 255 / max(1, gray(base)) * 0.8))
        return (g, g, g)

    def panel(region, fill, edge, stitch=True, highlight=None):
        x0, y0, x1, y1 = [int(round(v * 4)) for v in REGIONS[region]]
        for y in range(y0, y1):
            for x in range(x0, x1):
                c = jitter(fill, rnd)
                if x in (x0, x1 - 1) or y in (y0, y1 - 1):
                    c = edge
                elif highlight and y == y0 + 1:
                    c = highlight
                elif stitch and (x in (x0 + 2, x1 - 3) or y in (y0 + 2, y1 - 3)) and (x + y) % 2 == 0:
                    c = light
                px[x, y] = to_tint(c) + (255,) if region in TINTED else c + (255,)

    panel('body_front', base, dark)
    panel('body_back', base, dark)
    panel('body_side', base, dark)
    panel('body_top', base, dark, stitch=False)
    panel('flap_front', dark, tuple(max(0, v - 25) for v in dark), stitch=False, highlight=base)
    panel('flap_top', dark, tuple(max(0, v - 25) for v in dark))
    panel('flap_side', dark, tuple(max(0, v - 25) for v in dark), stitch=False)
    panel('pocket_front', light if name != 'netherite' else base, dark)
    panel('pocket_edge', dark, dark, stitch=False)
    panel('strap', strap, tuple(max(0, v - 20) for v in strap), stitch=False)
    panel('metal', metal, tuple(max(0, v - 50) for v in metal), stitch=False)
    if name in ('diamond', 'emerald'):  # a few sparkles
        for _ in range(10):
            x, y = rnd.randint(1, 18), rnd.randint(1, 22)
            px[x, y] = (255, 255, 255, 255)
    if name == 'ender':  # the eye on the pocket
        x0, y0 = 44 + 6, 24 + 4
        for dx in range(4):
            for dy in range(3):
                px[x0 + dx, y0 + dy] = (60, 200, 140, 255) if 0 < dx < 3 or dy == 1 else (20, 90, 70, 255)
    return img


def icon(name, src):
    if name == 'leather':
        return icons.leather_layers()[0]
    return icons.draw(name)[0]


HEADER = 20
SLOTS_TOP = 24


def bevel_panel(d, x0, y0, x1, y1, bg=(198, 198, 198)):
    """Vanilla style rounded panel: black outline, white top-left bevel, gray bottom-right bevel."""
    white, shade, edge = (255, 255, 255, 255), (85, 85, 85, 255), (0, 0, 0, 255)
    d.rectangle((x0 + 2, y0 + 1, x1 - 2, y1 - 1), fill=bg + (255,))
    d.rectangle((x0 + 1, y0 + 2, x1 - 1, y1 - 2), fill=bg + (255,))
    d.line((x0 + 2, y0, x1 - 3, y0), fill=edge)
    d.line((x0 + 3, y1, x1 - 2, y1), fill=edge)
    d.line((x0, y0 + 2, x0, y1 - 3), fill=edge)
    d.line((x1, y0 + 3, x1, y1 - 2), fill=edge)
    d.point([(x0 + 1, y0 + 1), (x1 - 2, y0 + 1), (x1 - 1, y0 + 2), (x0 + 1, y1 - 2), (x0 + 2, y1 - 1), (x1 - 1, y1 - 1)], fill=edge)
    d.line((x0 + 2, y0 + 1, x1 - 3, y0 + 1), fill=white)
    d.line((x0 + 1, y0 + 2, x0 + 1, y1 - 3), fill=white)
    d.point([(x0 + 2, y0 + 2)], fill=white)
    d.line((x0 + 3, y1 - 1, x1 - 2, y1 - 1), fill=shade)
    d.line((x1 - 1, y0 + 3, x1 - 1, y1 - 2), fill=shade)
    d.point([(x1 - 2, y1 - 2)], fill=shade)


def slot(d, x, y):
    d.rectangle((x, y, x + 17, y + 17), fill=(139, 139, 139, 255))
    d.line((x, y, x + 16, y), fill=(55, 55, 55, 255))
    d.line((x, y, x, y + 16), fill=(55, 55, 55, 255))
    d.line((x + 1, y + 17, x + 17, y + 17), fill=(255, 255, 255, 255))
    d.line((x + 17, y + 1, x + 17, y + 17), fill=(255, 255, 255, 255))


def gui(name, cols, rows):
    w = max(176, cols * 18 + 14)
    top = SLOTS_TOP + rows * 18 + 16
    h = top + 83
    img = Image.new('RGBA', (w + 28, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    o, dark, mid, light, high = icons.TIERS[name][:5]
    if name == 'leather':  # the icon is gray for the dye tint, the gui uses the default leather brown
        o, dark, mid, light, high = (58, 34, 18), (108, 66, 38), (160, 101, 64), (190, 132, 90), (214, 166, 122)
    # side tab for the buttons, tucked behind the main panel
    bevel_panel(d, w - 6, 22, w + 26, 22 + 4 * 22 + 6)
    for i in range(4):
        x, y = w + 2, 26 + i * 22
        d.rectangle((x - 1, y - 1, x + 20, y + 20), fill=(139, 139, 139, 255))
        d.line((x - 1, y - 1, x + 19, y - 1), fill=(55, 55, 55, 255))
        d.line((x - 1, y - 1, x - 1, y + 19), fill=(55, 55, 55, 255))
        d.line((x, y + 20, x + 20, y + 20), fill=(255, 255, 255, 255))
        d.line((x + 20, y, x + 20, y + 20), fill=(255, 255, 255, 255))
    bevel_panel(d, 0, 0, w - 1, h - 1)
    # coloured header with the tier's colours
    for y in range(3, HEADER):
        t = (y - 3) / (HEADER - 4)
        c = tuple(int(a + (b - a) * t) for a, b in zip(mid if name != 'netherite' else light, dark))
        d.line((3, y, w - 4, y), fill=c + (255,))
    d.line((3, 2, w - 4, 2), fill=high + (255,))
    d.line((3, HEADER, w - 4, HEADER), fill=o + (255,))
    d.line((3, HEADER + 1, w - 4, HEADER + 1), fill=(150, 150, 150, 255))
    left = (w - cols * 18) // 2
    for r in range(rows):
        for c in range(cols):
            slot(d, left + c * 18, SLOTS_TOP - 1 + r * 18)
    # groove between the backpack and your inventory
    gy = SLOTS_TOP + rows * 18 + 1
    d.line((6, gy, w - 7, gy), fill=(139, 139, 139, 255))
    d.line((6, gy + 1, w - 7, gy + 1), fill=(255, 255, 255, 255))
    inv_left = (w - 162) // 2
    for r in range(3):
        for c in range(9):
            slot(d, inv_left + c * 18, top - 1 + r * 18)
    for c in range(9):
        slot(d, inv_left + c * 18, top + 57)
    return img


def main():
    for d in ('item', 'gui'):
        os.makedirs(os.path.join(TEX, d), exist_ok=True)
    os.makedirs(os.path.join(ROOT, 'models', 'item'), exist_ok=True)
    os.makedirs(os.path.join(ROOT, 'items'), exist_ok=True)
    for name, (src, base, dark, light, strap, metal, cols, rows) in TIERS.items():
        tinted = name == 'leather'
        icon(name, src).save(os.path.join(TEX, 'item', name + '_backpack.png'))
        texture_3d(name, base, dark, light, strap, metal, tinted).save(os.path.join(TEX, 'item', name + '_backpack_3d.png'))
        json.dump(model_3d(name, tinted), open(os.path.join(ROOT, 'models', 'item', name + '_backpack_3d.json'), 'w'), indent=1)
        layers = {'layer0': 'backpack:item/' + name + '_backpack'}
        if tinted:
            icons.leather_layers()[1].save(os.path.join(TEX, 'item', name + '_backpack_overlay.png'))
            layers['layer1'] = 'backpack:item/' + name + '_backpack_overlay'
        json.dump({'parent': 'minecraft:item/generated', 'textures': layers},
                  open(os.path.join(ROOT, 'models', 'item', name + '_backpack.json'), 'w'), indent=1)
        tints = [{'type': 'minecraft:dye', 'default': LEATHER_DEFAULT - 0x1000000}] if tinted else None

        def m(model):
            out = {'type': 'minecraft:model', 'model': model}
            if tints:
                out['tints'] = tints
            return out
        definition = {'model': {'type': 'minecraft:select', 'property': 'minecraft:display_context',
                                'cases': [{'when': ['gui'], 'model': m('backpack:item/' + name + '_backpack')}],
                                'fallback': m('backpack:item/' + name + '_backpack_3d')}}
        json.dump(definition, open(os.path.join(ROOT, 'items', name + '_backpack.json'), 'w'), indent=1)
        if name != 'ender':
            gui(name, cols, rows).save(os.path.join(TEX, 'gui', name + '_backpack.png'))
    print('ok')


if __name__ == '__main__':
    main()
